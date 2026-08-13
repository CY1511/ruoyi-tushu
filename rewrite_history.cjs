const { execSync } = require('child_process');
const fs = require('fs');
process.chdir('D:/books/RuoYi-Vue-master');

// Step 1: Create orphan branch from current working tree
execSync('git checkout --orphan temp-branch', { stdio: 'inherit' });
execSync('git add -A', { stdio: 'inherit' });
execSync('git commit -m "feat: RuoYi 闲置教材交易平台 v1.0.0"', { stdio: 'inherit' });

// Step 2: Delete old branches and push new one
execSync('git branch -D main', { stdio: 'inherit' });
execSync('git branch -m temp-branch main', { stdio: 'inherit' });
execSync('git push github --force --delete main 2>/dev/null || true', { stdio: 'inherit' });
execSync('git push github -u main --force', { stdio: 'inherit' });
console.log('GitHub push done');

// Also push to Gitee
try {
  execSync('git push origin --force --delete main 2>/dev/null || true', { stdio: 'inherit' });
  execSync('git push origin -u main --force', { stdio: 'inherit' });
  console.log('Gitee push done');
} catch(e) {
  console.log('Gitee push skipped');
}

// Cleanup temp files
['commit_push.cjs','commit_push.sh','cleanup.cjs','cleanup2.cjs','done.cjs','final.cjs','final_push.cjs','final_push.sh','cleanup_done.cjs','done.cjs'].forEach(f => {
  try { fs.unlinkSync(f); } catch(e) {}
});

console.log('完成！提交历史已重写。');
