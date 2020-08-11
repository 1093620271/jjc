import req from '@/utils/request'
import { getCookie, setCookie, getDomainUrl } from '@/utils/tools'

const baseUrl = getDomainUrl() // 登录域名

// 登录时生成指定长度的xsrf_token
function randomWord(randomFlag, min, max) {
  let str = '',
    range = min,
    arr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
  // 随机产生
  if (randomFlag) {
    range = Math.round(Math.random() * (max - min)) + min
  }
  for (let i = 0; i < range; i++) {
    let pos = Math.round(Math.random() * (arr.length - 1))
    str += arr[pos]
  }
  return str
}

// 仅用于登录
export function idaasRequest(paramsObj) {
  let params = paramsObj || {}
  // xsrf处理
  let xsrf_token = getCookie('XSRF-TOKEN')
  if (!xsrf_token) {
    xsrf_token = randomWord(true, 16, 16)
    setCookie('XSRF-TOKEN', xsrf_token)
  }
  if (!params.headers) params.headers = {}
  params.headers['X-XSRF-TOKEN'] = xsrf_token
  params.url = baseUrl + params.url
  return req(params)
}
