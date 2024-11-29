#!/bin/bash

# 获取系统架构和操作系统名称，如果未指定则使用当前系统的值
arch=${1:-$(uname -m)}
os=${2:-$(uname -s)}

# 查找所有不符合格式的文件并重命名
for file in *; do
    if [[ -f "$file" && ! "$file" =~ .*-.*-.*\..* ]]; then
            mv "$file" "${file%.*}-${arch}-${os}.${file##*.}"
                fi

done
