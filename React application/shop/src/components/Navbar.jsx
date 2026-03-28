import { Link } from "react-router-dom";

function Navbar({keycloak}) {
  // use the passed-in instance rather than an undefined global
  const username = keycloak?.tokenParsed?.preferred_username || "Guest";
  return (
    <header className="bg-slate-800 border-b border-slate-700">
      <div className="site px-4 py-3 flex items-center justify-between relative">
        <Link to="/" className="text-lg font-semibold">
          Spring Boot Microservices Shop
        </Link>

        {/* Centered nav: Home + Add Product */}
        <nav className="hidden sm:flex gap-4 absolute left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2">
          <Link to="/" className="text-sm text-slate-200 hover:text-white">
            Home
          </Link> 
          {keycloak?.authenticated && (
            <Link to="/add-product" className="text-sm text-slate-200 hover:text-white">
              Add Product
            </Link>
          )}
        </nav>

        {/* Right corner: login/logout */}
        <div className="flex items-center gap-3 ml-auto">
          <div className="text-sm text-slate-300 text-right">
            {keycloak?.authenticated ? (
              <>
                <div className="font-medium">Hi {username}</div>
                <button
                  className="text-xs text-slate-400 hover:text-slate-200"
                  onClick={() => keycloak.logout()}
                >
                  Logout
                </button>
              </>
            ) : (
              <button
                className="text-xs text-slate-400 hover:text-slate-200"
                onClick={() => keycloak.login()}
              >
                Login
              </button>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

export default Navbar;