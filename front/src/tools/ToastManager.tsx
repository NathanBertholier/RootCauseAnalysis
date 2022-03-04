import React from "react";
import ReactDOM from "react-dom";
import MyToast, { ToastProps } from "./MyToast";

interface ToastOptions {
    id?: string;
    title: string
    content: string;
    duration?: number;
}

export class ToastManager {
    private readonly containerRef: HTMLDivElement;
    private toasts: ToastProps[] = [];

    /**
     * Create the toasts container
     */
    constructor() {
        const body : HTMLBodyElement = document.getElementsByTagName("body")[0];
        const toastContainer : HTMLDivElement = document.createElement("div");
        toastContainer.id = "toast-container-main";
        body.insertAdjacentElement("beforeend", toastContainer);
        this.containerRef = toastContainer;
    }

    /**
     * Add a new toast in the container
     * @param options
     */
    public show(options: ToastOptions): void {
        const toastId = Math.random().toString(36).slice(2, 11);
        const newToast: ToastProps = {
            id: toastId,
            ...options, // if id is passed within options, it will overwrite the auto-generated one
            destroy: () => this.destroy(options.id ?? toastId),
        };

        this.toasts = [newToast, ...this.toasts];
        this.render();
    }

    /***
     * Remove the toast with this specific ID and render the other
     * @param id The ID of the toast that should be destroyed
     */
    public destroy(id: string): void {
        this.toasts = this.toasts.filter((toastProps: ToastProps) => toastProps.id !== id);
        this.render();
    }

    /**
     * render all the toasts in the container.
     */
    private render(): void {
        const toastsList = this.toasts.map((toastProps: ToastProps) => (
            <MyToast key={toastProps.id} {...toastProps} />
        ));
        ReactDOM.render(toastsList, this.containerRef);
    }
}

export const toast = new ToastManager();