import React, { useEffect } from "react";
import {Toast} from "react-bootstrap";

export interface ToastProps {
    id: string;
    title: string
    destroy: () => void;
    content: string;
    duration?: number;
}

const MyToast: React.FC<ToastProps> = (props) => {
    const { destroy, title, content, duration = 0 } = props;

    useEffect(() => {
        if (!duration) return;

        const timer = setTimeout(() => {
            destroy();
        }, duration);

        return () => clearTimeout(timer);
    }, [destroy, duration]);

    return (
        <Toast onClose={destroy} >
            <Toast.Header className={ title === "Error" ? "toast-header-error" : "" } >
                <strong className="me-auto">{title}</strong>
            </Toast.Header>
            <Toast.Body>{content}</Toast.Body>
        </Toast>
    );
};

export default MyToast;