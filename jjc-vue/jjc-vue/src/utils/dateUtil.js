let now = new Date() // 当前日期
let nowDayOfWeek = now.getDay() // 今天本周的第几天
let nowDay = now.getDate() // 当前日
let nowMonth = now.getMonth() // 当前月
let nowYear = now.getYear() // 当前年
nowYear += (nowYear < 2000) ? 1900 : 0

export default {
  /**
   * 获得当前日期
   *
   * @returns
   */
  getNowDay() {
    return this.formatDate(new Date())
  },

  /**
   * 获得本周的开始时间
   *
   * @returns
   */
  getStartDayOfWeek() {
    let day = nowDayOfWeek || 7
    return this.formatDate(new Date(now.getFullYear(), nowMonth, nowDay + 1 - day))
  },
  /**
   * 获得本周的结束时间
   *
   * @returns
   */
  getEndDayOfWeek() {
    let day = nowDayOfWeek || 7
    return this.formatDate(new Date(now.getFullYear(), nowMonth, nowDay + 7 - day))
  },
  /**
   * 获得本月的开始时间
   *
   * @returns
   */
  getStartDayOfMonth() {
    let monthStartDate = new Date(nowYear, nowMonth, 1)
    return this.formatDate(monthStartDate)
  },
  /**
   * 获得本月的结束时间
   *
   * @returns
   */
  getEndDayOfMonth() {
    let monthEndDate = new Date(nowYear, nowMonth, this.getMonthDays())
    return this.formatDate(monthEndDate)
  },
  /**
   * 获得本月天数
   *
   * @returns
   */
  getMonthDays() {
    let monthStartDate = new Date(nowYear, nowMonth, 1)
    let monthEndDate = new Date(nowYear, nowMonth + 1, 1)
    let days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24)
    return days
  },
  /**
   * @param 日期格式化
   * @returns {String}
   */
    formatDate(date) {
      let myyear = date.getFullYear()
      let mymonth = date.getMonth() + 1
      let myweekday = date.getDate()

      if (mymonth < 10) {
        mymonth = '0' + mymonth
      }
      if (myweekday < 10) {
        myweekday = '0' + myweekday
      }
      return (myyear + '-' + mymonth + '-' + myweekday)
    }
}
