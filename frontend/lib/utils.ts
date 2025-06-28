import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"
import {toast} from "sonner";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export  const truncateContent = (content: string, maxLength: number = 100) => {
  if (content.length <= maxLength) return content;
  return content.substring(0, maxLength) + "...";
};

export   const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString();
};

// Copy to clipboard
export const copyToClipboard = async (content: string) => {
  try {
    await navigator.clipboard.writeText(content);
    toast.success("Copied to clipboard!");
  } catch (err) {
    toast.error("Failed to copy to clipboard");
  }
};
