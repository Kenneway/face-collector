var stompClient = null;
socketUrl = '/video/api/websocket/preview';

function connect() {
    var socket = new SockJS(socketUrl);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        successCallback();
    }, function () {
        connect();
    });
}

function successCallback() {
    stompClient.subscribe('/camera/01', function (greeting) {
        $("#frame").attr("src", "data:image/jpeg;base64," + greeting.body);
    });

    stompClient.subscribe('/camera/02', function (greeting) {
        $("#frame2").attr("src", "data:image/jpeg;base64," + greeting.body);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

$(function () {
});

connect();

