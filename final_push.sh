#!/bin/bash
cd "D:/books/RuoYi-Vue-master"
git add -A
git commit -m "chore: 清理临时脚本"
git push github main
git push origin main
rm -f final_push.cjs
echo "完成"
