import Vue from 'vue'
import Vuex from 'vuex'
import { login, getUserInfo } from '@/api/user'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: null
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
    }
  },
  actions: {
    // 登录
    Login({ commit }, loginForm) {
      return new Promise((resolve, reject) => {
        login(loginForm).then(res => {
          const token = res.token
          commit('SET_TOKEN', token)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 获取用户信息
    GetUserInfo({ commit }) {
      return new Promise((resolve, reject) => {
        getUserInfo().then(res => {
          const user = res.user
          commit('SET_USER_INFO', user)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 退出登录
    Logout({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        commit('SET_USER_INFO', null)
        resolve()
      })
    }
  },
  getters: {
    token: state => state.token,
    userInfo: state => state.userInfo
  }
})
