import Keycloak from "keycloak-js";
import axios from "axios"; // ← MUST import axios here

const keycloak = new Keycloak({
  url: "http://localhost:8181",   // your keycloak server
  realm: "spring-microservices-security-realm",
  clientId: "frontend-client"
});

// initialize Keycloak in a non‑blocking way and return the result
export const initKeycloak = async () => {
  // check if there's an existing SSO session but don't force login
  const authenticated = await keycloak.init({ onLoad: "check-sso" });
  if (authenticated) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${keycloak.token}`;
  }
  return authenticated;
};

export default keycloak;