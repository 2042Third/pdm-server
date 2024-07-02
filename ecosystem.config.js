
module.exports = {
    apps: [
        {
            name: 'pdm-server',

            // script: 'java',
            // args: '-jar target/pdm-server-0.0.1-SNAPSHOT.jar',
            script: './target/pdm-server',
            interpreter: 'none',
            exec_mode: 'fork_mode',
            instances: 1,
            autorestart: true,
            watch: false,
            max_memory_restart: '1G',
            kill_timeout : 3000,
            env: {
                NODE_ENV: 'development',
            },
            env_production: {
                NODE_ENV: 'production',
            },
        },
    ],
};