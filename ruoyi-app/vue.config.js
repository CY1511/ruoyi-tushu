module.exports = {
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
