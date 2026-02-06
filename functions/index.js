/**
 * Firebase Cloud Functions - Push Notification Handler
 *
 * Triggers on new messages, appointments, and cancellations
 * to send FCM push notifications.
 *
 * SETUP: Replace functions/index.js with this file, then:
 *   cd functions && npm install && firebase deploy --only functions
 */

const { onDocumentCreated, onDocumentUpdated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");

// Initialize Firebase Admin SDK
initializeApp();

const db = getFirestore();
const messaging = getMessaging();

/**
 * Triggered when a new message is created in any chat.
 * Sends a push notification to the recipient.
 */
exports.onNewMessage = onDocumentCreated(
  "chats/{chatId}/messages/{messageId}",
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      console.log("No data associated with the event");
      return;
    }

    const messageData = snapshot.data();
    const chatId = event.params.chatId;
    const senderId = messageData.senderId;

    console.log(`New message in chat ${chatId} from ${senderId}`);

    try {
      // 1. Get the chat document to find participants
      const chatDoc = await db.collection("chats").doc(chatId).get();

      if (!chatDoc.exists) {
        console.log("Chat document not found");
        return;
      }

      const participants = chatDoc.data().participants;

      if (!participants || participants.length < 2) {
        console.log("Invalid participants list");
        return;
      }

      // 2. Find the recipient (the participant who is NOT the sender)
      const recipientId = participants.find((id) => id !== senderId);

      if (!recipientId) {
        console.log("Could not determine recipient");
        return;
      }

      // 3. Get recipient's FCM token
      const recipientDoc = await db.collection("users").doc(recipientId).get();

      if (!recipientDoc.exists) {
        console.log(`Recipient user document not found: ${recipientId}`);
        return;
      }

      const recipientData = recipientDoc.data();
      const fcmToken = recipientData.fcmToken;

      if (!fcmToken) {
        console.log(`No FCM token for recipient: ${recipientId}`);
        return;
      }

      // 4. Get sender's name
      const senderDoc = await db.collection("users").doc(senderId).get();
      let senderName = "Someone";
      let senderRole = "";

      if (senderDoc.exists) {
        const senderData = senderDoc.data();
        senderName = senderData.name || "Unknown";
        senderRole = senderData.role || "";

        // Prefix "Dr." for doctors
        if (senderRole === "DOCTOR") {
          senderName = `Dr. ${senderName}`;
        }
      }

      // 5. Determine notification body based on message type
      let notificationBody = "";
      const messageType = messageData.type || "TEXT";

      switch (messageType) {
        case "TEXT":
          notificationBody =
            messageData.text && messageData.text.length > 100
              ? messageData.text.substring(0, 100) + "..."
              : messageData.text || "New message";
          break;

        case "MEDIA":
          notificationBody = `📎 ${messageData.fileName || "Sent a file"}`;
          break;

        case "LIVE_LOCATION":
          notificationBody = "📍 Shared live location";
          break;

        default:
          notificationBody = "New message";
      }

      // 6. Send FCM notification
      const fcmMessage = {
        token: fcmToken,
        notification: {
          title: senderName,
          body: notificationBody,
        },
        data: {
          chatId: chatId,
          senderId: senderId,
          senderName: senderName,
          type: "NEW_MESSAGE",
          messageType: messageType,
        },
        android: {
          priority: "high",
          notification: {
            channelId: "messages_channel",
            icon: "ic_notification",
            clickAction: "OPEN_CHAT",
          },
        },
      };

      const response = await messaging.send(fcmMessage);
      console.log(`Notification sent successfully: ${response}`);
    } catch (error) {
      if (
        error.code === "messaging/invalid-registration-token" ||
        error.code === "messaging/registration-token-not-registered"
      ) {
        console.log("Invalid token for recipient, removing token");

        const chatDoc = await db.collection("chats").doc(chatId).get();
        if (chatDoc.exists) {
          const participants = chatDoc.data().participants;
          const recipientId = participants.find((id) => id !== senderId);
          if (recipientId) {
            await db
              .collection("users")
              .doc(recipientId)
              .update({ fcmToken: null });
          }
        }
      } else {
        console.error("Error sending notification:", error);
      }
    }
  }
);

/**
 * Triggered when a new appointment is created.
 * Notifies the doctor.
 */
exports.onNewAppointment = onDocumentCreated(
  "appointments/{appointmentId}",
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) return;

    const appointmentData = snapshot.data();
    const doctorUid = appointmentData.doctorUid;
    const patientUid = appointmentData.patientUid;
    const date = appointmentData.date || "";
    const timeSlot = appointmentData.timeSlot || "";

    if (!doctorUid || !patientUid) return;

    try {
      const doctorDoc = await db.collection("users").doc(doctorUid).get();
      if (!doctorDoc.exists) return;

      const fcmToken = doctorDoc.data().fcmToken;
      if (!fcmToken) return;

      const patientDoc = await db.collection("users").doc(patientUid).get();
      const patientName = patientDoc.exists
        ? patientDoc.data().name || "A patient"
        : "A patient";

      await messaging.send({
        token: fcmToken,
        notification: {
          title: "New Appointment Booked",
          body: `${patientName} booked an appointment on ${date} at ${timeSlot}`,
        },
        data: {
          type: "NEW_APPOINTMENT",
          appointmentId: event.params.appointmentId,
          patientUid: patientUid,
        },
        android: {
          priority: "high",
          notification: {
            channelId: "appointments_channel",
            icon: "ic_notification",
          },
        },
      });

      console.log("Appointment notification sent to doctor");
    } catch (error) {
      console.error("Error sending appointment notification:", error);
    }
  }
);

/**
 * Triggered when an appointment status changes to "cancelled".
 * Notifies the doctor.
 */
exports.onAppointmentCancelled = onDocumentUpdated(
  "appointments/{appointmentId}",
  async (event) => {
    const beforeData = event.data.before.data();
    const afterData = event.data.after.data();

    // Only trigger if status changed to "cancelled"
    if (beforeData.status === afterData.status) return;
    if (afterData.status !== "cancelled") return;

    const doctorUid = afterData.doctorUid;
    const patientUid = afterData.patientUid;
    const date = afterData.date || "";
    const timeSlot = afterData.timeSlot || "";

    try {
      if (doctorUid) {
        const doctorDoc = await db.collection("users").doc(doctorUid).get();
        if (doctorDoc.exists) {
          const fcmToken = doctorDoc.data().fcmToken;
          if (fcmToken) {
            const patientDoc = await db
              .collection("users")
              .doc(patientUid)
              .get();
            const patientName = patientDoc.exists
              ? patientDoc.data().name || "A patient"
              : "A patient";

            await messaging.send({
              token: fcmToken,
              notification: {
                title: "Appointment Cancelled",
                body: `${patientName} cancelled their appointment on ${date} at ${timeSlot}`,
              },
              data: {
                type: "APPOINTMENT_CANCELLED",
                appointmentId: event.params.appointmentId,
              },
              android: {
                priority: "high",
                notification: {
                  channelId: "appointments_channel",
                },
              },
            });
          }
        }
      }
    } catch (error) {
      console.error("Error sending cancellation notification:", error);
    }
  }
);