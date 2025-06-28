import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    output: "export",
    // Since we're serving from Spring Boot's static folder
    basePath: "",
    // Disable image optimization for static export
    images: {
        unoptimized: true
    },
    // Ensure trailing slashes for static export
    trailingSlash: true
};

export default nextConfig;
