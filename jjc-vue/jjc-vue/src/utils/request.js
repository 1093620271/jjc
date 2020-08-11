import axios from 'axios'
import { Message } from 'element-ui'

const errorCode = {
  '12000': '需要验证码',
  '12001': 'sessionid无效',
  '12002': '短信重发过于频繁',
  '12003': '短信验证码错误',
  '12004': '短信验证码失败次数太多，需要重新发起流程',
  '12005': '流程错误，通常是步骤错了',
  '12006': '重复注册',
  '12007': '系统错误',
  // '12008': '帐号未注册(废除)',
  '12008': '帐号未注册',
  '12009': '恶意注册拒绝',
  '12010': '发短信失败(超频)',
  '12011': '验证码答案验证失败',
  '12012': '用户名或密码错误',
  '12013': '恶意登录拒绝',
  '12014': '不支持非国内手机号',
  '12015': '没有权限',
  '12016': '无效的参数',
  '12017': '图片识别失败',
  '12018': '登录态无效',
  '12019': '票据userid不匹配',
  '12020': '票据过期',
  '12021': 'url无效',
  '12022': '资料错误',
  '12023': '身份证信息无效',
  '12024': '恶意重置',
  '12025': '恶意设置PIN码',
  '12026': 'PIN码已存在',
  '12027': 'PIN码不存在',
  '12028': 'PIN码恶意',
  '12029': '无效的accessToken',
  '12030': 'open帐号未绑定',
  '12031': 'open帐号已绑定',
  '12032': '无效的open appid',
  '12033': 'SN已存在',
  '12034': 'SN不存在',
  '12035': '扫码登录已确认',
  '12036': '需要更多验证因子',
  '12037': '无效的jws',
  '12038': '仅允许user调用',
  '12039': '需要刷新票据',
  '12040': '发送邮件失败',
  '12041': '高德id已绑定',
  '12042': '通过VIN没有查到车辆信息',
  '12043': '车已绑定',
  '12044': '未找到用户',
  '12045': '设备不存在',
  '12046': 'signature无效',
  '12047': 'secretId不存在',
  '12048': 'secretId无权限',
  '12049': 'CAM子用户无权限登录',
  '12050': '需要重置密码',
  '12051': '云API临时token无效',
  '12052': '云API临时token过期参数值无效',
  '12053': '未找到IDP配置',
  '12054': '无效的id_token',
  '12055': '无效的密码格式',
  '12056': '未找到第三方Oauth应用access_token',
  '12057': '验第三方access_token失败',
  '12058': '第三方Oauth配置出错',
  '12059': '未找到第三方Oauth用户access_token',
  '12060': '不允许获取第三方Oauth用户access_token',
  '12061': 'appid配置未找到',
  '12062': 'appid配置已存在',
  '12063': 's2密码盐错误',
  '12064': '小程序核身接口失败',
  '12065': '用户身份不一致',
  '12066': 'alias别名不存在',
  '12067': '第三方code过期',
  '12068': '小程序userInfo未找到unionId'
}

const service = axios.create({
  timeout: 5000
  // 允许携带cookie
  // withCredentials: true
})

service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    // eslint-disable-next-line no-undefined
    if (res.code !== undefined) {
      if (res.code === 0 || res.code.length === 6) {
        res.ErrorCode = 'OK'
      } else if (String(res.code).length === 5) {
        res.message = errorCode[res.code]
      }
    }
    if (res.ErrorCode !== 'OK') {
      Message({
        message: res.message || res.ErrorMsg || 'Error',
        type: 'error',
        duration: 2 * 1000
      })
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    Message({
      message: error.message,
      type: 'error',
      duration: 2 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
