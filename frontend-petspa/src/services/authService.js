import axios from 'axios'

const API_URL = 'http://localhost:8080/auth/'

export default {
  async login(credentials) {
    const response = await axios.post(API_URL + 'login', credentials)
    if (response.data.token) {
      localStorage.setItem('user', JSON.stringify(response.data))
    }
    return response.data
  },
  logout() {
    localStorage.removeItem('user')
  }
}