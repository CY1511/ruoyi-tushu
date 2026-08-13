const fs = require('fs');
const { execSync } = require('child_process');
process.chdir('D:/books/RuoYi-Vue-master');

// Delete all temp scripts
const temps = ['commit_push.cjs','commit_push.sh','cleanup.cjs','cleanup2.cjs','done.cjs','final.cjs','final_push.cjs','final_push.sh'];
temps.forEach(f => { try { fs.unlinkSync(f); console.log('Deleted:', f); } catch(e) {} });

// Stage and commit
execSync('git add -A', { stdio: 'inherit' });
execSync('git commit -m "chore: 清理临时脚本文件"', { stdio: 'inherit' });
execSync('git push github main', { stdio: 'inherit' });
console.log('Pushed to GitHub');
