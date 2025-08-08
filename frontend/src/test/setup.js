import { vi } from 'vitest'

// Mock localStorage with actual implementation
const localStorageMock = {
  data: {},
  getItem: vi.fn((key) => localStorageMock.data[key] || null),
  setItem: vi.fn((key, value) => {
    localStorageMock.data[key] = value
  }),
  removeItem: vi.fn((key) => {
    delete localStorageMock.data[key]
  }),
  clear: vi.fn(() => {
    localStorageMock.data = {}
  })
}

global.localStorage = localStorageMock

global.fetch = vi.fn()

// Clear localStorage before each test
beforeEach(() => {
  localStorageMock.clear()
  vi.clearAllMocks()
})

// Mock router
vi.mock('vue-router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    replace: vi.fn(),
    back: vi.fn(),
    go: vi.fn()
  })),
  useRoute: vi.fn(() => ({
    path: '/',
    name: 'Home',
    params: {},
    query: {},
    meta: {}
  }))
}))

// Console cleanup for tests
const originalError = console.error
beforeAll(() => {
  console.error = (...args) => {
    if (
      typeof args[0] === 'string' &&
      args[0].includes('Warning: ReactDOM.render is no longer supported')
    ) {
      return
    }
    originalError.call(console, ...args)
  }
})

afterAll(() => {
  console.error = originalError
})
