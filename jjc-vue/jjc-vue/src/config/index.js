module.exports = {
  development: {
    domain: 'localhost:8089', //请求地址
  },
  test: {
    domain: 'zstdev.qq.com',
    cgiUrl: 'https://enroll-apidev.learn.tencent.com',
    qidianQaUrl: 'https://embed.qidian.qq.com/zhiku/qalib/index?pagemode=1&token='
  },
  production: {
    domain: 'zstdev.qq.com',
    cgiUrl: 'https://enroll-apidev.learn.tencent.com',
    qidianQaUrl: 'https://embed.qidian.qq.com/zhiku/qalib/index?pagemode=1&token='
  }
}
