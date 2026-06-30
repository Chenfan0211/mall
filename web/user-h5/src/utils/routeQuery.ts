export function currentRouteQuery() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> } | undefined;
    const query: Record<string, string> = { ...(current?.options || {}) };
    if (typeof window === 'undefined') {
        return query;
    }
    const hash = window.location.hash || '';
    const queryText = hash.includes('?') ? hash.slice(hash.indexOf('?') + 1) : '';
    if (!queryText) {
        return query;
    }
    new URLSearchParams(queryText).forEach((value, key) => {
        query[key] = value;
    });
    return query;
}
