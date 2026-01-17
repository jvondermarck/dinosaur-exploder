type Feature = {
  icon: string;
  title: string;
};

export default function FeatureSection({dict}: {dict: Feature[] }) {
  return (
    <section className="max-w-4xl mx-auto w-full py-8 px-4 grid grid-cols-1 md:grid-cols-3 gap-4">
      {dict.map((feature, idx) => (
        <div
          key={idx}
          className="bg-black/80 border-2 border-green-700 rounded-lg p-6 text-center text-green-100"
        >
          <span className="block mb-2 text-2xl">{feature.icon}</span>
          <span className="font-retro uppercase tracking-wider text-green-400">{feature.title}</span>
        </div>
      ))}
    </section>
  );
}