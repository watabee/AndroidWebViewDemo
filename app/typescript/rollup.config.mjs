import terser from '@rollup/plugin-terser';
import typescript from '@rollup/plugin-typescript';
import tsconfig from "./tsconfig.json" assert { type: "json" };

export default {
    input: 'src/index.ts',
    output: {
        name: 'main',
        format: 'umd',
    },
    plugins: [
        typescript({
            ...tsconfig.compilerOptions,
            include: '**/*.{js,ts}'
        }),
        terser({
            keep_fnames: true
        })
    ]
};
