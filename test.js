import http from "k6/http";
import { check } from "k6";

export const options = {
  batchPerHost: 10,
  scenarios: {
    category_scenario: {
      executor: "shared-iterations",
      startTime: "0s",
      vus: 10,
      iterations: 2000,
      maxDuration: "10s",
    },
  },
};

export default function () {
  const response = http.get("http://localhost:8080/api/product");
  check(response, {
    "is status 200": (r) => r.status === 200,
  });
}