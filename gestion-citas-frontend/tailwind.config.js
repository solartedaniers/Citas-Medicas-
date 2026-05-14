/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['"DM Sans"', 'ui-sans-serif', 'system-ui'],
        display: ['"Sora"', 'ui-sans-serif'],
      }
    },
  },
  plugins: [],
}
