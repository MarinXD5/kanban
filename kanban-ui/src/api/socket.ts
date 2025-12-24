import SockJS from "sockjs-client";
import { Client, type IMessage } from "@stomp/stompjs";
import type { TaskEvent } from "../types/TaskEvent";

export const connectSocket = (
  onMessage: (event: TaskEvent) => void
) =>{
  const client = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/ws"),

    onConnect: () =>{
      client.subscribe("/topic/tasks", (message: IMessage) =>{
        const event: TaskEvent = JSON.parse(message.body);
        onMessage(event);
      })
    },
  });

  client.activate();
  return client;
}