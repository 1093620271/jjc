// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.config.productionTip = false

Vue.use(ElementUI)

// 全局注册tools方法
import * as funcs from './utils/tools'

for (let name in funcs) {
  Vue.prototype[name] = funcs[name]
}

import dateUtil from './utils/dateUtil.js'

Vue.prototype.dateUtil = dateUtil

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
