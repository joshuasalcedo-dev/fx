const { app, BrowserWindow, Menu, ipcMain, Tray, globalShortcut, nativeImage, screen } = require("electron");
const serve = require("electron-serve");
const path = require("path");

// Missing serve import fixed
const appServe = app.isPackaged ? serve({
  directory: path.join(__dirname, "../out")
}) : null;

const dimension = {
  width: 400,  // Narrower width (was 800)
  height: 450, // Reduced height (was 600)
}
// Declare these at the top level so they're accessible everywhere
let win = null;
let tray = null;

const createWindow = () => {
  win = new BrowserWindow({
    width: dimension.width,
    height: dimension.height,
    minWidth: dimension.width,
    minHeight: dimension.height,
    maxWidth: dimension.width,
    maxHeight: dimension.height,
    transparent: false,
    opacity: 0.9,
    frame: false, // Should be false for system tray apps
    show: false, // Don't show initially
    skipTaskbar: true, // Don't show in taskbar
    alwaysOnTop: true, // Keep on top like clipboard
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      nodeIntegration: false,
      contextIsolation: true
    },
    backgroundColor: "#000000"
  });

  // Remove menu bar
  Menu.setApplicationMenu(null);

  if (app.isPackaged) {
    appServe(win).then(() => {
      win.loadURL("app://-");
    });
  } else {
    win.loadURL("http://localhost:3000");
    win.webContents.on("did-fail-load", (e, code, desc) => {
      win.webContents.reloadIgnoringCache();
    });
  }

  win.on('blur', () => {
    if (!win.webContents.isDevToolsOpened()) {
      win.hide();
    }
  });

  // Prevent window from being closed, just hide it
  win.on('close', (event) => {
    if (!app.isQuitting) {
      event.preventDefault();
      win.hide();
    }
  });
}

// In main.js
ipcMain.on('set-always-on-top', (event, flag) => {
  win.setAlwaysOnTop(flag);
  // When pinned, don't hide on blur
  if (flag) {
    win.removeAllListeners('blur');
  } else {
    // Re-add blur listener when unpinned
    win.on('blur', () => {
      if (!win.webContents.isDevToolsOpened()) {
        win.hide();
      }
    });
  }
});

const createTray = () => {
  // Create tray icon (you'll need to add an icon file)
  const iconPath = path.join(__dirname, 'assets/icon.png'); // Add your icon
  tray = new Tray(nativeImage.createFromPath(iconPath));

  const contextMenu = Menu.buildFromTemplate([
    {
      label: 'Show App',
      click: () => {
        toggleWindow();
      }
    },
    {
      type: 'separator'
    },
    {
      label: 'Quit',
      click: () => {
        app.isQuitting = true;
        app.quit();
      }
    }
  ]);

  tray.setToolTip('My App');
  tray.setContextMenu(contextMenu);

  // Click on tray icon toggles window
  tray.on('click', () => {
    toggleWindow();
  });
}

const toggleWindow = () => {
  if (win.isVisible()) {
    win.hide();
  } else {
    showWindow();
  }
}

const showWindow = () => {
  // Get cursor position
  const cursor = screen.getCursorScreenPoint();
  const display = screen.getDisplayNearestPoint(cursor);

  // Calculate position near cursor (offset slightly so cursor isn't covered)
  let x = cursor.x + 10; // 10px to the right of cursor
  let y = cursor.y + 10; // 10px below cursor

  // Make sure window doesn't go off screen
  // Check right edge
  if (x + dimension.width > display.bounds.x + display.bounds.width) {
    x = cursor.x - dimension.width - 10; // Show to the left of cursor instead
  }

  // Check bottom edge
  if (y + dimension.height > display.bounds.y + display.bounds.height) {
    y = cursor.y - dimension.height - 10; // Show above cursor instead
  }

  // Check left edge
  if (x < display.bounds.x) {
    x = display.bounds.x;
  }

  // Check top edge
  if (y < display.bounds.y) {
    y = display.bounds.y;
  }

  win.setPosition(x, y);
  win.show();
  win.focus();
}

app.whenReady().then(() => {
  createWindow();
  createTray();

  // Register global shortcut (e.g., Ctrl+Shift+V)
  const ret = globalShortcut.register('CommandOrControl+Shift+V', () => {
    toggleWindow();
  });

  if (!ret) {
    console.log('Registration failed');
  }
});

app.on("window-all-closed", () => {
  // Don't quit when window is closed for tray apps
  // if(process.platform !== "darwin"){
  //   app.quit();
  // }
});

// Handle IPC events
ipcMain.on('window-minimize', (event) => {
  win.hide(); // Hide instead of minimize for tray apps
});

ipcMain.on('window-maximize', (event) => {
  // For tray apps, you might not want maximize
  win.isMaximized() ? win.unmaximize() : win.maximize();
});

ipcMain.on('window-close', (event) => {
  win.hide(); // Hide instead of close
});

app.on('before-quit', () => {
  app.isQuitting = true;
});

app.on('will-quit', () => {
  // Unregister all shortcuts
  globalShortcut.unregisterAll();
});