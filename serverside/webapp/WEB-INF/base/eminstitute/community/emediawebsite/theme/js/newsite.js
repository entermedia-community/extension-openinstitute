const prefersReducedMotion = window.matchMedia(
	"(prefers-reduced-motion: reduce)",
).matches;

if (!prefersReducedMotion && window.gsap && window.ScrollTrigger) {
	gsap.registerPlugin(ScrollTrigger);

	gsap.set(".reveal", {
		y: 36,
		opacity: 0,
	});

	gsap.to(".site-header", {
		y: 16,
		opacity: 1,
		duration: 0.8,
		ease: "power2.out",
		scrollTrigger: {
			trigger: ".site-header",
			start: "top top",
			scrub: true,
		},
	});

	// gsap.to(".site-header", {
	// 	y: 0,
	// 	opacity: 0.95,
	// 	duration: 0.8,
	// 	ease: "power2.out",
	// 	scrollTrigger: {
	// 		trigger: ".site-header",
	// 		start: "top top",
	// 		end: "+=200",
	// 		scrub: true,
	// 	},
	// });

	const heroTimeline = gsap.timeline({ defaults: { ease: "power3.out" } });

	heroTimeline
		.from(".brand, .site-nav a, .site-header .button", {
			y: -18,
			opacity: 0,
			duration: 0.6,
			stagger: 0.08,
		})
		.to(
			".hero .reveal",
			{
				y: 0,
				opacity: 1,
				duration: 0.9,
				stagger: 0.12,
			},
			"-=0.25",
		)
		.from(
			".hero-card--primary",
			{
				scale: 0.9,
				rotation: -5,
				opacity: 0,
				duration: 1,
			},
			"-=0.8",
		)
		.from(
			".hero-card--secondary, .hero-badge",
			{
				y: 40,
				opacity: 0,
				duration: 0.9,
				stagger: 0.1,
			},
			"-=0.65",
		);

	gsap.utils.toArray(".section").forEach((section) => {
		const reveals = section.querySelectorAll(".reveal:not(.engine-card)");

		if (reveals.length && !section.classList.contains("hero")) {
			gsap.to(reveals, {
				y: 0,
				opacity: 1,
				duration: 0.9,
				stagger: 0.08,
				ease: "power2.out",
				scrollTrigger: {
					trigger: section,
					start: "top 75%",
				},
			});
		}
	});

	gsap.utils.toArray(".parallax").forEach((element) => {
		const speed = Number(element.dataset.speed || 0.18);

		gsap.fromTo(
			element,
			{ yPercent: -speed * 100 },
			{
				yPercent: speed * 100,
				ease: "none",
				scrollTrigger: {
					trigger: element,
					start: "top bottom",
					end: "bottom top",
					scrub: true,
				},
			},
		);
	});

	const enginesCards = gsap.utils.toArray(".engine-card");

	gsap.fromTo(
		enginesCards,
		{
			y: 60,
			opacity: 0,
			rotateX: -10,
		},
		{
			y: 0,
			opacity: 1,
			rotateX: 0,
			duration: 0.9,
			stagger: 0.12,
			ease: "power2.out",
			scrollTrigger: {
				trigger: "#engines",
				start: "top 65%",
			},
		},
	);

	gsap.fromTo(
		".ownership-panel",
		{ y: 24 },
		{
			y: 0,
			ease: "none",
			scrollTrigger: {
				trigger: "#ownership",
				start: "top bottom",
				end: "bottom top",
				scrub: true,
			},
		},
	);
}
