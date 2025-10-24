cmake_minimum_required(VERSION 3.10)
project("rand")

# 生成共享库（库名随机化，与 Java 层加载对应）
add_library(
        rand${RANDOM}  # 随机库名（实际编译时可手动指定，如 rand123）
        SHARED
        hidden_crash.cpp
)

# 链接系统库
find_library(
        log-lib
        log
)

target_link_libraries(
        rand${RANDOM}
        ${log-lib}
)

# 关键：剥离符号表和调试信息
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -s")
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -s")