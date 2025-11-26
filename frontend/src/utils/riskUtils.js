export const getRiskColor = (level) => {
    const colors = {
        LOW: 'var(--color-low)',
        MEDIUM: 'var(--color-medium)',
        HIGH: 'var(--color-high)',
        CRITICAL: 'var(--color-critical)',
    };
    return colors[level] || 'var(--color-text-muted)';
};

export const getRiskLabel = (level) => {
    return level ? level.charAt(0) + level.slice(1).toLowerCase() : 'Unknown';
};
