import { useContext } from "react";
import { toastContext } from "./ToastProvider";

export function useToast(){
    return useContext(toastContext)
}