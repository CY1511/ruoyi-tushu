// 后端存的是 `/profile/xxx` 这样的相对路径，渲染时必须拼上接口前缀：
// 开发环境 H5 跑在 84 端口，`/profile` 只有走 `/dev-api` 前缀才会被 vue.config.js 的代理转发到 8081；
// 生产环境同理要依赖 nginx 对 `/prod-api` 的转发。少了这一步，C 端所有商品图都是 404。
// （管理端 ruoyi-ui 的 ImagePreview 组件一直是这么处理的。）
const BASE_API = process.env.VUE_APP_BASE_API || '/dev-api'

export function withBase(src) {
  if (!src) return ''
  const url = String(src)
  if (/^(https?:|data:|blob:)/.test(url)) return url
  if (url.indexOf(BASE_API) === 0) return url
  return BASE_API + (url.charAt(0) === '/' ? url : '/' + url)
}

// cover_image 是多张图用英文逗号拼起来的字符串
export function toImageList(coverImage) {
  if (!coverImage) return []
  return String(coverImage).split(',').filter(Boolean).map(withBase)
}
