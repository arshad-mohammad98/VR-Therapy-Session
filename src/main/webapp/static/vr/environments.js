/**
 * A-Frame WebXR Procedural Scene Builder
 * Drives 3D environments according to JSON configuration supplied by the Java backend.
 */

// Register custom breathing guide component
if (typeof AFRAME !== 'undefined') {
    AFRAME.registerComponent('breathing-guide', {
        schema: {
            minRadius: { type: 'number', default: 0.35 },
            maxRadius: { type: 'number', default: 0.95 },
            inhaleTime: { type: 'number', default: 4000 },
            holdTime: { type: 'number', default: 4000 },
            exhaleTime: { type: 'number', default: 4000 }
        },
        init: function () {
            this.state = 'inhale';
            this.stateStartTime = Date.now();
            this.mesh = this.el.getObject3D('mesh');
        },
        tick: function () {
            const now = Date.now();
            const elapsed = now - this.stateStartTime;
            let currentRadius = this.data.minRadius;

            if (this.state === 'inhale') {
                const progress = Math.min(1.0, elapsed / this.data.inhaleTime);
                currentRadius = this.data.minRadius + (this.data.maxRadius - this.data.minRadius) * Math.sin(progress * Math.PI / 2);
                if (elapsed >= this.data.inhaleTime) {
                    this.state = 'hold';
                    this.stateStartTime = now;
                    this.updatePrompt('Hold Breath...');
                } else {
                    this.updatePrompt('Inhale Gently...');
                }
            } else if (this.state === 'hold') {
                currentRadius = this.data.maxRadius;
                if (elapsed >= this.data.holdTime) {
                    this.state = 'exhale';
                    this.stateStartTime = now;
                    this.updatePrompt('Exhale Slowly...');
                }
            } else if (this.state === 'exhale') {
                const progress = Math.min(1.0, elapsed / this.data.exhaleTime);
                currentRadius = this.data.maxRadius - (this.data.maxRadius - this.data.minRadius) * Math.sin(progress * Math.PI / 2);
                if (elapsed >= this.data.exhaleTime) {
                    this.state = 'inhale';
                    this.stateStartTime = now;
                    this.updatePrompt('Inhale Gently...');
                }
            }

            this.el.setAttribute('radius', currentRadius);
        },
        updatePrompt: function (text) {
            const promptEl = document.getElementById('breathing-text');
            if (promptEl) {
                promptEl.setAttribute('value', text);
            }
        }
    });
}

function buildVrScene(sceneEl, config) {
    if (!sceneEl || !config) return;

    const skyColor = config.skyColor || '#87ceeb';
    const groundColor = config.groundColor || '#e8d5b5';
    const type = config.environmentType || 'CALM_BEACH';
    const intensity = config.intensity || 2;

    // 1. Sky & Sun
    let sky = document.getElementById('vr-sky');
    if (sky) {
        sky.setAttribute('color', skyColor);
    }

    // 2. Clear previous dynamic entities
    const container = document.getElementById('dynamic-entities');
    if (!container) return;
    container.innerHTML = '';

    // 3. Environment-specific procedural elements
    if (type === 'CALM_BEACH') {
        // Sand plane
        const sand = document.createElement('a-plane');
        sand.setAttribute('position', '0 0 0');
        sand.setAttribute('rotation', '-90 0 0');
        sand.setAttribute('width', '100');
        sand.setAttribute('height', '100');
        sand.setAttribute('color', groundColor);
        container.appendChild(sand);

        // Water ocean plane
        const ocean = document.createElement('a-plane');
        ocean.setAttribute('position', '0 0.05 -15');
        ocean.setAttribute('rotation', '-90 0 0');
        ocean.setAttribute('width', '120');
        ocean.setAttribute('height', '60');
        ocean.setAttribute('color', '#38bdf8');
        ocean.setAttribute('material', 'opacity: 0.85; transparent: true; roughness: 0.1');
        container.appendChild(ocean);

        // Procedural palm trees / gentle rocks
        for (let i = -15; i <= 15; i += 6) {
            const trunk = document.createElement('a-cylinder');
            trunk.setAttribute('position', `${i} 2.5 -8`);
            trunk.setAttribute('radius', '0.25');
            trunk.setAttribute('height', '5');
            trunk.setAttribute('color', '#854d0e');
            container.appendChild(trunk);

            const crown = document.createElement('a-cone');
            crown.setAttribute('position', `${i} 5.2 -8`);
            crown.setAttribute('radius-bottom', '1.8');
            crown.setAttribute('radius-top', '0');
            crown.setAttribute('height', '2.5');
            crown.setAttribute('color', '#15803d');
            container.appendChild(crown);
        }
    } else if (type === 'FOREST_WALK') {
        // Forest ground moss
        const ground = document.createElement('a-plane');
        ground.setAttribute('rotation', '-90 0 0');
        ground.setAttribute('width', '100');
        ground.setAttribute('height', '100');
        ground.setAttribute('color', groundColor);
        container.appendChild(ground);

        // Procedural trees along a path
        for (let z = -20; z <= 10; z += 4) {
            [-4, 4].forEach(x => {
                const trunk = document.createElement('a-cylinder');
                trunk.setAttribute('position', `${x} 2.5 ${z}`);
                trunk.setAttribute('radius', '0.3');
                trunk.setAttribute('height', '5');
                trunk.setAttribute('color', '#5c3a21');
                container.appendChild(trunk);

                const foliage = document.createElement('a-cone');
                foliage.setAttribute('position', `${x} 5.5 ${z}`);
                foliage.setAttribute('radius-bottom', '2');
                foliage.setAttribute('radius-top', '0.2');
                foliage.setAttribute('height', '4');
                foliage.setAttribute('color', '#1e5f38');
                container.appendChild(foliage);
            });
        }
    } else if (type === 'HEIGHTS_EXPOSURE') {
        // High observation platform
        const platformHeight = config.platformHeightMeters || (10 + intensity * 25);
        const deck = document.createElement('a-box');
        deck.setAttribute('position', '0 0 0');
        deck.setAttribute('width', '8');
        deck.setAttribute('height', '0.4');
        deck.setAttribute('depth', '10');
        deck.setAttribute('color', '#64748b');
        container.appendChild(deck);

        // Transparent glass railing
        const rail = document.createElement('a-box');
        rail.setAttribute('position', '0 0.8 -4.8');
        rail.setAttribute('width', '7.8');
        rail.setAttribute('height', '1.4');
        rail.setAttribute('depth', '0.08');
        rail.setAttribute('material', 'color: #94a3b8; opacity: 0.4; transparent: true');
        container.appendChild(rail);

        // Ground landscape below to simulate height perspective
        const deepGround = document.createElement('a-plane');
        deepGround.setAttribute('position', `0 ${-platformHeight} 0`);
        deepGround.setAttribute('rotation', '-90 0 0');
        deepGround.setAttribute('width', '250');
        deepGround.setAttribute('height', '250');
        deepGround.setAttribute('color', '#334155');
        container.appendChild(deepGround);
    } else if (type === 'SOCIAL_GATHERING') {
        // Terrace deck
        const deck = document.createElement('a-plane');
        deck.setAttribute('rotation', '-90 0 0');
        deck.setAttribute('width', '50');
        deck.setAttribute('height', '50');
        deck.setAttribute('color', groundColor);
        container.appendChild(deck);

        // Simulated café avatars
        const avatarCount = config.avatarCount || 6;
        for (let i = 0; i < avatarCount; i++) {
            const angle = (i / avatarCount) * Math.PI * 2;
            const dist = 3.5;
            const x = Math.cos(angle) * dist;
            const z = Math.sin(angle) * dist - 3;

            const body = document.createElement('a-cylinder');
            body.setAttribute('position', `${x} 1 ${z}`);
            body.setAttribute('radius', '0.35');
            body.setAttribute('height', '1.4');
            body.setAttribute('color', i % 2 === 0 ? '#3b82f6' : '#ec4899');
            container.appendChild(body);

            const head = document.createElement('a-sphere');
            head.setAttribute('position', `${x} 1.9 ${z}`);
            head.setAttribute('radius', '0.22');
            head.setAttribute('color', '#fcd34d');
            container.appendChild(head);
        }
    } else if (type === 'BREATHING_ROOM') {
        // Obsidian dark reflective sanctuary
        const floor = document.createElement('a-plane');
        floor.setAttribute('rotation', '-90 0 0');
        floor.setAttribute('width', '40');
        floor.setAttribute('height', '40');
        floor.setAttribute('color', '#090d16');
        floor.setAttribute('material', 'roughness: 0.1; metalness: 0.8');
        container.appendChild(floor);

        // Ambient glowing rings
        for (let r = 2; r <= 8; r += 2) {
            const ring = document.createElement('a-ring');
            ring.setAttribute('rotation', '-90 0 0');
            ring.setAttribute('position', '0 0.05 0');
            ring.setAttribute('radius-inner', `${r}`);
            ring.setAttribute('radius-outer', `${r + 0.1}`);
            ring.setAttribute('color', '#06b6d4');
            ring.setAttribute('material', 'opacity: 0.4; transparent: true');
            container.appendChild(ring);
        }
    } else if (type === 'NIGHT_SKY_SLEEP_ROOM') {
        // Dark meadow floor with night stars and aurora
        const floor = document.createElement('a-plane');
        floor.setAttribute('rotation', '-90 0 0');
        floor.setAttribute('width', '100');
        floor.setAttribute('height', '100');
        floor.setAttribute('color', '#050b14');
        floor.setAttribute('material', 'roughness: 0.9; metalness: 0.1');
        container.appendChild(floor);

        // Constellation starfield
        for (let i = 0; i < 60; i++) {
            const star = document.createElement('a-sphere');
            const x = (Math.random() - 0.5) * 80;
            const y = 12 + Math.random() * 25;
            const z = (Math.random() - 0.5) * 80;
            star.setAttribute('position', `${x} ${y} ${z}`);
            star.setAttribute('radius', `${0.08 + Math.random() * 0.15}`);
            star.setAttribute('color', '#ffffff');
            star.setAttribute('material', 'emissive: #93c5fd; emissiveIntensity: 1; transparent: true; opacity: 0.9');
            container.appendChild(star);
        }

        // Crescent moon
        const moon = document.createElement('a-sphere');
        moon.setAttribute('position', '15 22 -35');
        moon.setAttribute('radius', '2.5');
        moon.setAttribute('color', '#fef3c7');
        moon.setAttribute('material', 'emissive: #fde68a; emissiveIntensity: 0.7');
        container.appendChild(moon);
    }

    // 4. Guided Breathing Sphere
    if (config.guidedBreathingEnabled) {
        const breathingSphere = document.createElement('a-sphere');
        breathingSphere.setAttribute('id', 'vr-breathing-sphere');
        breathingSphere.setAttribute('position', '0 1.6 -3');
        breathingSphere.setAttribute('radius', '0.5');
        breathingSphere.setAttribute('material', 'color: #38bdf8; opacity: 0.8; transparent: true; emissive: #0284c7; emissiveIntensity: 0.6');
        breathingSphere.setAttribute('breathing-guide', 'minRadius: 0.35; maxRadius: 0.95; inhaleTime: 4000; holdTime: 4000; exhaleTime: 4000');
        container.appendChild(breathingSphere);

        // 3D text prompt above sphere
        const text = document.createElement('a-text');
        text.setAttribute('id', 'breathing-text');
        text.setAttribute('value', 'Inhale Gently...');
        text.setAttribute('position', '0 2.8 -3');
        text.setAttribute('align', 'center');
        text.setAttribute('color', '#ffffff');
        text.setAttribute('scale', '1.2 1.2 1.2');
        container.appendChild(text);
    }
}
