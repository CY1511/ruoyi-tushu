module.exports = {
  // 生产包不带 .map，否则前端源码会被完整还原到线上
  productionSourceMap: false,
  devServer: {
    port: 84,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: { '^/dev-api': '' }
      }
    }
  },
  lintOnSave: false
}
