import { fetchWithAuth } from "/js/auth/auth.js";
import { loadPage } from "./module/page-manager.js";

export async function getModLog() {
    const res = await fetchWithAuth("/api/user/modLog");
    if (res.ok) {
        const data = await res.json();

        const container = document.querySelector("moderation-log");
        const log = document.querySelector(".log-item");
        const logCopy = log.cloneNode(true);
        log.remove();
        data.actionList.forEach((item) => {
            const itemAppend = logCopy.cloneNode(true);

            //@ts-ignore
            const logUser = itemAppend.querySelector("#log-name");
            //@ts-ignore
            const logActionType = itemAppend.querySelector("#log-action");
            //@ts-ignore
            const logMessage = itemAppend.querySelector("#log-message");
            //@ts-ignore
            const logTimestamp = itemAppend.querySelector("#log-timestamp");

            logUser.textContent = item.username;
            logActionType.textContent = item.action_type;
            logMessage.textContent = item.action_message;
            const timeText =
                item.action_time_stamp[0].toString() +
                ":" +
                item.action_time_stamp[1].toString() +
                ":" +
                item.action_time_stamp[2].toString();
            logTimestamp.textContent = timeText;

            container.append(itemAppend);
        });
    } else {
        console.log("Something wrong");
        loadPage("/workspace/home");
    }
}
