import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export const connectSocket = (onMessage: () => void) => {
  const client = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
    onConnect: () => {
      client.subscribe("/topic/tasks", () => {
        onMessage();
      });
    },
  });

  client.activate();
  return client;
};
