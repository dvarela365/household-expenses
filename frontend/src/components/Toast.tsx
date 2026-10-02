import { useEffect } from 'react'

type ToastProps = {
  message: string
  onDismiss: () => void
}

export default function Toast({ message, onDismiss }: ToastProps) {
  useEffect(() => {
    const timeout = setTimeout(onDismiss, 2500)
    return () => clearTimeout(timeout)
  }, [onDismiss])

  return (
    <div
      role="status"
      className="fixed bottom-24 left-1/2 -translate-x-1/2 rounded-full bg-gray-900 px-4 py-2 text-sm text-white shadow-lg"
    >
      {message}
    </div>
  )
}
