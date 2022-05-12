import Keycloak from "keycloak-js";
import { atom } from "recoil";

export const keycloak = atom<Keycloak>({
  key: "keycloakInternal", // unique ID (with respect to other atoms/selectors)
  default: new Keycloak(),
});
