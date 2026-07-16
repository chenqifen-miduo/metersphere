import MSR from '@/api/http/index';
import {
  MiduoSsoBridgeUrl,
  MiduoSsoCallbackUrl,
  MiduoSsoLogoutUrl,
  MiduoSsoStateUrl,
  MiduoSsoStatusUrl,
} from '@/api/requrls/sso/miduo';

export interface MiduoSsoStatus {
  enabled: boolean;
  ready: boolean;
  reason?: string;
  message?: string;
}

export interface MiduoSsoCallbackBody {
  token: string;
  state: string;
}

export function getMiduoSsoStatus() {
  return MSR.get<MiduoSsoStatus>({ url: MiduoSsoStatusUrl }, { ignoreCancelToken: true });
}

export function getMiduoSsoState() {
  return MSR.get<{ state: string }>({ url: MiduoSsoStateUrl });
}

export function postMiduoSsoCallback(data: MiduoSsoCallbackBody) {
  return MSR.post({ url: MiduoSsoCallbackUrl, data });
}

export function postMiduoSsoLogout() {
  return MSR.post({ url: MiduoSsoLogoutUrl });
}

export function getMiduoBridgeUrl() {
  return MSR.get<{ url: string }>({ url: MiduoSsoBridgeUrl });
}
