var _a, _b;
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
var port = Number((_b = (_a = globalThis.process) === null || _a === void 0 ? void 0 : _a.env) === null || _b === void 0 ? void 0 : _b.PORT) || 5173;
export default defineConfig({
    plugins: [react()],
    server: { host: '0.0.0.0', port: port },
    preview: { host: '0.0.0.0', port: port },
});
