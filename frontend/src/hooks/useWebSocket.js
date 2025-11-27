import { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

/**
 * Custom hook for WebSocket connection
 * Connects to backend WebSocket server and subscribes to topics
 * 
 * @param {string} topic - The topic to subscribe to (e.g., '/topic/alerts')
 * @returns {Object} { messages, isConnected, error }
 */
export const useWebSocket = (topic) => {
    const [messages, setMessages] = useState([]);
    const [isConnected, setIsConnected] = useState(false);
    const [error, setError] = useState(null);
    const clientRef = useRef(null);

    useEffect(() => {
        // Create WebSocket client with SockJS
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,

            onConnect: () => {
                console.log('WebSocket Connected');
                setIsConnected(true);
                setError(null);

                // Subscribe to the topic
                client.subscribe(topic, (message) => {
                    try {
                        const data = JSON.parse(message.body);
                        console.log('Received WebSocket message:', data);
                        setMessages((prev) => [...prev, data]);
                    } catch (err) {
                        console.error('Error parsing WebSocket message:', err);
                    }
                });
            },

            onDisconnect: () => {
                console.log('WebSocket Disconnected');
                setIsConnected(false);
            },

            onStompError: (frame) => {
                console.error('WebSocket Error:', frame);
                setError('WebSocket connection error');
                setIsConnected(false);
            },
        });

        clientRef.current = client;
        client.activate();

        // Cleanup on unmount
        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, [topic]);

    return { messages, isConnected, error };
};
