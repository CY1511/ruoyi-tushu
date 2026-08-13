const { execSync } = require('child_process');
process.chdir('D:/books/RuoYi-Vue-master');
execSync('git add -A', { stdio: 'inherit' });
execSync('git commit -m "chore: 清理临时脚本"', { stdio: 'inherit' });
execSync('git push github main', { stdio: 'inherit' });
execSync('git push origin main', { stdio: 'inherit' });
execSync('rm final_push.sh', { stdio: 'inherit' });
console.log('完成');
