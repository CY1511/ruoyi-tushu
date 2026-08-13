import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueTreeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

Vue.use(ElementUI)
Vue.component('treeselect', VueTreeselect)

Vue.config.productionTip = false

const token = store.state.token
if (token) {
  store.dispatch('GetUserInfo').catch(() => {
    store.commit('SET_TOKEN', '')
    store.commit('SET_USER_INFO', null)
    localStorage.removeItem('token')
  })
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
