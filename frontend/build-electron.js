#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

console.log('🚀 Building frontend for Electron...');

// Step 1: Build the Next.js app
console.log('📦 Building Next.js app...');
try {
  execSync('npm run build', { stdio: 'inherit' });
  console.log('✅ Next.js build completed');
} catch (error) {
  console.error('❌ Next.js build failed:', error.message);
  process.exit(1);
}

// Step 2: Copy built files to Electron app
const sourceDir = path.join(__dirname, 'out');
const targetDir = path.join(__dirname, '..', 'clipboard-electron', 'out');

console.log('📁 Copying files to Electron app...');

// Remove existing directory if it exists
if (fs.existsSync(targetDir)) {
  console.log('🗑️  Removing existing files...');
  fs.rmSync(targetDir, { recursive: true, force: true });
}

// Copy the out directory
try {
  copyDirectory(sourceDir, targetDir);
  console.log('✅ Files copied successfully');
} catch (error) {
  console.error('❌ Failed to copy files:', error.message);
  process.exit(1);
}

console.log('🎉 Frontend build for Electron completed!');

// Helper function to recursively copy directories
function copyDirectory(src, dest) {
  if (!fs.existsSync(src)) {
    throw new Error(`Source directory does not exist: ${src}`);
  }
  
  fs.mkdirSync(dest, { recursive: true });
  
  const entries = fs.readdirSync(src, { withFileTypes: true });
  
  for (let entry of entries) {
    const srcPath = path.join(src, entry.name);
    const destPath = path.join(dest, entry.name);
    
    if (entry.isDirectory()) {
      copyDirectory(srcPath, destPath);
    } else {
      fs.copyFileSync(srcPath, destPath);
    }
  }
}