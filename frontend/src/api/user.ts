export interface LoginForm {
  username: string
  password: string
  remember?: boolean
  captcha?: string
  uuid?: string
}

export interface RegisterForm {
  username: string
  email: string
  password: string
  confirmPassword: string
  agreement?: boolean
}

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  email?: string
  avatar?: string
  role?: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}