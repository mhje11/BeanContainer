const ws = new WebSocket("ws://"+window.location.host+"/ws");

ws.onmessage = (event)=>{
    const chatMessage = document.getElementById("chatMessages");
    const chatElement = document.createElement("p");
    chatElement.innerText = event.data;
    chatMessage.appendChild(chatElement);
    chatMessage.scrollTop = chatMessage.scrollHeight;
}

const sendMessage = () =>{
    const message = document.getElementById("message").value;
    if(message.trim() !== ""){
        ws.send(message);
        document.getElementById("message").value = '';
        document.getElementById("message").focus();
    }
};

//엔터가 입력 되었을 때 똑같이 동작되도록.
document.getElementById("message").addEventListener("keypress", (event)=>{
    if(event.key === "Enter"){
        sendMessage();
        event.preventDefault();
    }
});

window.onload=()=>{
    document.getElementById("message").focus();
}