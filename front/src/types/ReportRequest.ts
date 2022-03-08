export type ReportParams = {
    cache?: true
    delta?: number
    expanded: true
    network_size?: number
    proximity_limit?: number
}

export type ReportRequest = {
    params: ReportParams
}