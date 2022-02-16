import React, { useEffect } from "react";
import {Col, Container, Row} from "react-bootstrap";

export interface ToastProps {
    id: string;
    destroy: () => void;
    content: string;
    duration?: number;
}

const Toast: React.FC<ToastProps> = (props) => {
    const { destroy, content, duration = 0 } = props;

    useEffect(() => {
        if (!duration) return;

        const timer = setTimeout(() => {
            destroy();
        }, duration);

        return () => clearTimeout(timer);
    }, [destroy, duration]);

    return (
        <Container fluid>
            <Row className="toast-content-wrapper">
                <Col className={"p-0 toast-content"} sm={11}>
                    {content}
                </Col>
                <Col className={ "p-0 toast-close" } sm={1}>
                    <button onClick={destroy}>X</button>
                </Col>
            </Row>
        </Container>
    );
};

export default Toast;