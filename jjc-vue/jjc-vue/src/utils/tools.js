import axios from 'axios'
import json2csv from 'json2csv'

const config = require('../config')
const env = development
const envInfo = config[env]

export function getDomainUrl() {
  return 'https://' + envInfo.domain
}

export function getLoginUrl() {
  return getDomainUrl() + '/login'
}



export function getCookie(name) {
  let arr = document.cookie.match(new RegExp('(^| )' + name + '=([^;]*)(;|$)'))
  if (arr != null) {
    return unescape(arr[2])
  }
  return null
}

export function setCookie(name, value, expires, path, domain) {
  if (!expires) { // 默认1天有效期
    expires = 24 * 60 * 60
  }
  let exp = new Date()
  exp.setTime(exp.getTime() + expires * 1000)
  let cookieName = name + '=' + escape(value) + ';expires=' + exp.toGMTString() + ';path=/'
  if (domain) {
    cookieName += ';domain=' + domain
  }
  document.cookie = cookieName
}

export function timeFormat(time, format) {
  if (!(time instanceof Date)) time = new Date(time)
  let o = {
    'M+': time.getMonth() + 1, // 月份
    'd+': time.getDate(), // 日
    'h+': time.getHours(), // 小时
    'm+': time.getMinutes(), // 分
    's+': time.getSeconds(), // 秒
    'q+': Math.floor((time.getMonth() + 3) / 3), // 季度
    'S': time.getMilliseconds() // 毫秒
  }
  if (/(y+)/.test(format)) {
    format = format.replace(RegExp.$1, (time.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  for (let k in o) {
    if (new RegExp('(' + k + ')').test(format)) {
      format = format.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)))
    }
  }
  return format
}

export function getQuery(name) {
  let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)')
  let r = window.location.search.substr(1).match(reg)
  if (r != null) return unescape(r[2])
  return null
}

function axiosGetParam(param) {
  let url = param.url
  delete param.url
  return {
    url: url,
    param: param,
    headers: {
      'Content-type': 'application/json',
      Token: getCookie('Token') || ''
    }
  }
}

export function axiosGet(param) {
  let options = axiosGetParam(param)
  options.url = envInfo.cgiUrl + options.url
  return new Promise((resolve, reject) => {
    axios.get(options.url, {
      params: options.param,
      headers: options.headers
    }).then(res => {
      let resp = res.data
      if (resp.Response.Error) {
        reject(resp.Response.Error.Message)
      } else {
        resolve(resp.Response)
      }
    }).catch(err => {
      if (err.response.data.code == 401) {
        setCookie('Token', '')
        location.href = getLoginUrl()
      }
      reject(err)
    })
  })
}

function axiosPostParam(param) {
  let url = param.url
  delete param.url
  let tParam = param.param || param
  return {
    url: url,
    param: tParam,
    headers: {
      'Content-type': 'application/json',
      Token: getCookie('Token') || ''
    }
  }
}

export function axiosPost(param) {
  let options = axiosPostParam(param)
  options.url = envInfo.cgiUrl + options.url
  return new Promise((resolve, reject) => {
    axios.post(options.url, options.param, {
      headers: options.headers
    }).then(res => {
      let resp = res.data
      if (resp.Response.Error) {
        reject(resp.Response.Error.Message)
      } else {
        resolve(resp.Response)
      }
    }).catch(err => {
      if (err.response.data.code == 401) {
        setCookie('Token', '')
        location.href = getLoginUrl()
      }
      reject(err)
    })
  })
}

export function axiosPostOrigin(param) {
  let options = axiosPostParam(param)
  options.url = envInfo.cgiUrl + options.url
  return new Promise((resolve, reject) => {
    axios.post(options.url, options.param, {
      headers: options.headers
    }).then(res => {
      resolve(res.data)
    }).catch(err => {
      if (err.response.data.code == 401) {
        setCookie('Token', '')
        location.href = getLoginUrl()
      }
      reject(err)
    })
  })
}

export function login(cb) {
  let token = getCookie('Token')
  if (location.hash.indexOf('#/login') < 0 && !token) {
    location.href = getLoginUrl()
  }
  cb()
}

function myBrowserIsIE() {
  // 判断是否IE浏览器
  let isIE = false
  if (
    navigator.userAgent.indexOf('compatible') > -1 &&
    navigator.userAgent.indexOf('MSIE') > -1
  ) {
    // ie浏览器
    isIE = true
  }
  if (navigator.userAgent.indexOf('Trident') > -1) {
    // edge 浏览器
    isIE = true
  }
  return isIE
}

function createDownLoadClick(content, fileName) {
  // 创建a链接下载
  const link = document.createElement('a')
  link.href = encodeURI(content)
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

