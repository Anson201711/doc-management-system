import { describe, it, expect } from 'vitest'
import { formatDate, validateEmail, validatePassword, generateId, formatFileSize, debounce, throttle, deepClone } from '../utils/helpers'

describe('工具函数测试', () => {
  describe('formatDate 日期格式化', () => {
    it('应该正确格式化日期', () => {
      const date = new Date('2024-01-15T10:30:00')
      const formatted = formatDate(date)
      expect(formatted).toContain('2024')
    })

    it('应该处理空日期', () => {
      expect(formatDate(null)).toBe('-')
      expect(formatDate(undefined)).toBe('-')
    })

    it('应该处理字符串日期', () => {
      expect(formatDate('2024-01-15')).toBe('2024-01-15')
    })
  })

  describe('validateEmail 邮箱验证', () => {
    it('应该验证有效邮箱', () => {
      expect(validateEmail('test@example.com')).toBe(true)
      expect(validateEmail('user.name@domain.co.uk')).toBe(true)
      expect(validateEmail('admin@company.io')).toBe(true)
    })

    it('应该拒绝无效邮箱', () => {
      expect(validateEmail('invalid')).toBe(false)
      expect(validateEmail('@example.com')).toBe(false)
      expect(validateEmail('test@')).toBe(false)
      expect(validateEmail('test @example.com')).toBe(false)
      expect(validateEmail('')).toBe(false)
    })
  })

  describe('validatePassword 密码验证', () => {
    it('应该验证有效密码', () => {
      expect(validatePassword('Ab123456')).toBe(true)
      expect(validatePassword('Password1')).toBe(true)
      expect(validatePassword('Test1234')).toBe(true)
    })

    it('应该拒绝弱密码', () => {
      expect(validatePassword('short')).toBe(false)
      expect(validatePassword('nodigits')).toBe(false)
      expect(validatePassword('12345678')).toBe(false)
      expect(validatePassword('NoNumbers')).toBe(false)
      expect(validatePassword('')).toBe(false)
    })
  })

  describe('generateId 生成ID', () => {
    it('应该生成唯一ID', () => {
      const id1 = generateId()
      const id2 = generateId()
      expect(id1).not.toBe(id2)
      expect(id1.length).toBeGreaterThan(0)
    })
  })

  describe('formatFileSize 文件大小格式化', () => {
    it('应该正确格式化字节', () => {
      expect(formatFileSize(0)).toBe('0 B')
      expect(formatFileSize(1024)).toBe('1.00 KB')
      expect(formatFileSize(1048576)).toBe('1.00 MB')
      expect(formatFileSize(1073741824)).toBe('1.00 GB')
    })
  })

  describe('debounce 防抖函数', () => {
    it('应该延迟执行', async () => {
      let count = 0
      const fn = debounce(() => { count++ }, 100)
      fn()
      fn()
      fn()
      expect(count).toBe(0)
      await new Promise(resolve => setTimeout(resolve, 150))
      expect(count).toBe(1)
    })
  })

  describe('throttle 节流函数', () => {
    it('应该在限制时间内只执行一次', async () => {
      let count = 0
      const fn = throttle(() => { count++ }, 100)
      fn()
      fn()
      fn()
      expect(count).toBe(1)
      await new Promise(resolve => setTimeout(resolve, 150))
      fn()
      expect(count).toBe(2)
    })
  })

  describe('deepClone 深拷贝', () => {
    it('应该正确拷贝对象', () => {
      const obj = { a: 1, b: { c: 2 } }
      const cloned = deepClone(obj)
      expect(cloned).toEqual(obj)
      expect(cloned.b).not.toBe(obj.b)
    })

    it('应该正确拷贝数组', () => {
      const arr = [1, 2, { a: 3 }]
      const cloned = deepClone(arr)
      expect(cloned).toEqual(arr)
      expect(cloned[2]).not.toBe(arr[2])
    })

    it('应该处理原始类型', () => {
      expect(deepClone(null)).toBe(null)
      expect(deepClone(42)).toBe(42)
      expect(deepClone('test')).toBe('test')
    })
  })
})