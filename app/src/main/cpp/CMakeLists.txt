cmake_minimum_required(VERSION 3.22.1)

project("mobSec")

add_library(
        nativekeys
        SHARED
        nativekeys.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        nativekeys
        ${log-lib}
)


target_link_options(
        nativekeys
        PRIVATE
        "-Wl,-z,max-page-size=16384"
)
