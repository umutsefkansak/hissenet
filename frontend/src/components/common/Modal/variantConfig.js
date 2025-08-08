import { CheckCircle, XCircle, AlertTriangle, HelpCircle } from 'lucide-react';

/**
 * Configuration for modal variants: icon component and primary color.
 */
export const MODAL_VARIANTS = {
  success: { Icon: CheckCircle, color: '#4caf50' },
  error:   { Icon: XCircle, color: '#f44336' },
  warning: { Icon: AlertTriangle, color: '#ff9800' },
  confirm: { Icon: HelpCircle, color: '#ff9800' },
};