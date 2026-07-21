import './Button.css'

const Button = ({ value, onClick, hasSelection, customize, title }) => {
    return (
        <button className={`${customize != undefined ? customize : ''} btnComponent`} disabled={hasSelection} onClick={onClick} title={title}>
            {value}
        </button>
    )
}

export default Button;