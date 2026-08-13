const { execSync } = require('child_process');
process.chdir('D:/books/RuoYi-Vue-master');
execSync('rm -f commit_push.cjs commit_push.sh final.cjs final_push.cjs final_push.sh cleanup.cjs', { stdio: 'inherit' });
execSync('git add -A', { stdio: 'inherit' });
execSync('git commit -m "chore: 清理所有临时脚本文件"', { stdio: 'inherit' });
execSync('git push github main', { stdio: 'inherit' });
execSync('git push origin main', { stdio: 'inherit' });
console.log('完成');
