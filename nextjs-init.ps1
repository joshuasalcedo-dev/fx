# Initialize shadcn in the current directory
pnpm dlx shadcn@latest init

# Change to frontend directory
cd frontend

# Add shadcn components (specify which ones you need)
# Examples:
# pnpm dlx shadcn@latest add button
# pnpm dlx shadcn@latest add card
# pnpm dlx shadcn@latest add input
# Or add all components:
 pnpm dlx shadcn@latest add --all

# Install dependencies
pnpm install

# Run approve-builds
pnpm approve-builds

# Run the fix-chart PowerShell script

